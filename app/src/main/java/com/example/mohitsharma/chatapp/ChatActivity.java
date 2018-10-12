package com.example.mohitsharma.chatapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class ChatActivity extends AppCompatActivity {

    private String mChatUser;
    private Toolbar mChatToolbar;
    private DatabaseReference mRootRef;
    private DatabaseReference mrootRef;
    private TextView mTitleView;
    private TextView mLastSeenView;
    private CircleImageView mProfileImage;
    private FirebaseAuth mAuth;
    private String mCurrentUserId;
    private ImageButton mChatSendBtn;
    private EditText mChatMessageview;
    private RecyclerView mMessgaesList;
    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    private MessageAdapter mAdapter;
    private static final int TOTAL_ITEMS_TO_LOAD = 10;
    private int mCurrentPage = 1;
    private SwipeRefreshLayout mRefreshLayout;
    private static final int GALLERY_PICK = 1;
    private int itemPos = 0;
    private String mLastKey = "";
    private String mPrevKey = "";
    private ImageButton mChataddBtn;
    private StorageReference mImageStorage;
    private   String download_Url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        mChatToolbar = (Toolbar) findViewById(R.id.chat_app_bar);
        setSupportActionBar(mChatToolbar);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        mChatUser = getIntent().getStringExtra("user_id");
        String UserName = getIntent().getStringExtra("user_name");
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        mChatSendBtn = (ImageButton)findViewById(R.id.send_message);

        mrootRef = FirebaseDatabase.getInstance().getReference();
        mrootRef.keepSynced(true);

        mRootRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mChatUser);
        mRootRef.keepSynced(true);
        getSupportActionBar().setTitle(UserName);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_ber_view = inflater.inflate(R.layout.chat_custom_bar, null);
        actionBar.setCustomView(action_ber_view);

        mTitleView = (TextView) findViewById(R.id.chat_display_name);
        mLastSeenView = (TextView) findViewById(R.id.textView6);
        mProfileImage = (CircleImageView) findViewById(R.id.circleImageView2);
        mChatMessageview = (EditText)findViewById(R.id.get_message);
        mMessgaesList = (RecyclerView)findViewById(R.id.messages_list);
        mRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.message_swipe_layout);
        mChataddBtn = (ImageButton)findViewById(R.id.add_message);
        mAdapter = new MessageAdapter(messagesList);
        mImageStorage = FirebaseStorage.getInstance().getReference();


        mLinearLayout = new LinearLayoutManager(this);

        mMessgaesList.setLayoutManager(mLinearLayout);
        mMessgaesList.setAdapter(mAdapter);
        loadMessages();
          //mLastSeenView.setText(mChatUser);

        //................ Custom Action Bar Items .............

        mTitleView.setText(UserName);


        mRootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String online = dataSnapshot.child("LastSeen").getValue().toString();
                String check = dataSnapshot.child("online").getValue().toString();
                String image = dataSnapshot.child("thum_image").getValue().toString();
                Picasso.get().load(image).placeholder(R.drawable.profilec).into(mProfileImage);
                if(!check.equals("true")){

                    GetTimeAgo gettimeAgo = new GetTimeAgo();
                    long lasttime = Long.parseLong(online);
                    String lasttimeseen = gettimeAgo.getTimeAgo(lasttime,getApplicationContext());
                    mLastSeenView.setText(lasttimeseen);
                   // mLastSeenView.setText("Online");
                }else {
                    mLastSeenView.setText("Online");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



      //  DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        //databaseReference.child("Chat").child(mCurrentUserId).


        mChatSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               mrootRef.child("Chat").child(mCurrentUserId).addChildEventListener(new ChildEventListener() {
                   @Override
                   public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                       if(!dataSnapshot.hasChild(mChatUser)){
                           Map chatAddMap = new HashMap();
                           chatAddMap.put("seen",false);
                           chatAddMap.put("timestamp",ServerValue.TIMESTAMP);

                           Map chatUserMap = new HashMap();
                           chatUserMap.put("Chat/"+mCurrentUserId+"/"+mChatUser,chatAddMap);

                           chatUserMap.put("Chat/"+mChatUser+"/"+mCurrentUserId,chatAddMap);
                           mrootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                               @Override
                               public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                   if(databaseError!=null){
                                       Log.d("Chat_Log",databaseError.getMessage().toString());
                                   }
                               }
                           });
                       }

                   }

                   @Override
                   public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                   }

                   @Override
                   public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                   }

                   @Override
                   public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError databaseError) {

                   }
               });


                sendMessage();
            }
        });

        mChataddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent,"SELECT IMAGE"),GALLERY_PICK);
            }
        });



        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentPage++;
                itemPos=0;
                //loadMoreMessages();
                mRefreshLayout.setRefreshing(false);
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode,int resultcode,Intent data){
        super.onActivityResult(requestCode,resultcode,data);
        if(requestCode==GALLERY_PICK&&resultcode==RESULT_OK){
            final Uri imageUri = data.getData();
            //final Uri ImageUri = data.getUri();
            assert imageUri != null;
            File thumb_filepath = new File(imageUri.getPath());

            final String current_user_ref = "messages/"+mCurrentUserId+"/"+mChatUser;
            final String chat_user_ref = "messages/"+mChatUser+"/"+mCurrentUserId;

            DatabaseReference user_message_path = mrootRef.child("messages").child(mCurrentUserId).child(mChatUser).push();

            final String push_id = user_message_path.getKey();

            final StorageReference filepath = mImageStorage.child("message_images").child(push_id+".jpg");


            filepath.putFile(imageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
                        Toast.makeText(ChatActivity.this,"Profile Picture Updated",Toast.LENGTH_SHORT).show();




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
                final StorageReference thumbstorage_filepath = mImageStorage.child("message_images").child("thumbs").child(push_id+".jpg");


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
                          //  mDatabaseReference.child("thum_image").setValue(thumbuser_image);

                            Map messageMap = new HashMap();
                            messageMap.put("message",thumbuser_image);
                            messageMap.put("seend",false);
                            messageMap.put("type","image");
                            messageMap.put("time",ServerValue.TIMESTAMP);
                            messageMap.put("from",mCurrentUserId);
                            messageMap.put("to",mChatUser);

                            Map messageUserMap = new HashMap();
                            messageUserMap.put(current_user_ref+"/"+push_id,messageMap);
                            messageUserMap.put(chat_user_ref+"/"+push_id,messageMap);
                            mChatMessageview.setText("");

                            mrootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    if(databaseError!=null){
                                        Log.d("Chat_Log",databaseError.getMessage().toString());
                                    }
                                }
                            });
                        } else {
                            // Handle failures   Toast.makeText(SettingsActivity.this,"Profile Picture Updated",Toast.LENGTH_SHORT).show();
                            // ...
                            Toast.makeText(ChatActivity.this,"Picture NOt Updated",Toast.LENGTH_SHORT).show();
                        }
                    }
                });









            } catch (IOException e) {
                e.printStackTrace();
            }









        }
    }

    private void loadMoreMessages() {
        DatabaseReference messageRef = mrootRef.child("messages").child(mCurrentUserId).child(mChatUser);
        Query messageQuery = messageRef.orderByKey().endAt(mLastKey).limitToLast(10);
        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Messages message = dataSnapshot.getValue(Messages.class);
                String messageKey = dataSnapshot.getKey();
                if(!mPrevKey.equals(messageKey)) {
                    messagesList.add(itemPos++, message);
                }else {mPrevKey=messageKey;}
                if(itemPos == 1){

                    mLastKey = messageKey;

                }



                mAdapter.notifyDataSetChanged();

                mRefreshLayout.setRefreshing(false);
                mLinearLayout.scrollToPositionWithOffset(10,0);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadMessages() {
        DatabaseReference messageRef = mrootRef.child("messages").child(mCurrentUserId).child(mChatUser);
        Query messageQuery = messageRef.limitToLast(mCurrentPage*TOTAL_ITEMS_TO_LOAD);


        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Messages message = dataSnapshot.getValue(Messages.class);
                     itemPos++;
                     if(itemPos == 1){
                         String messageKey = dataSnapshot.getKey();
                         mLastKey = messageKey;
                         mPrevKey = messageKey;
                     }
                    messagesList.add(message);
                    mAdapter.notifyDataSetChanged();
                    mMessgaesList.scrollToPosition(messagesList.size()-1);
                    mRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage() {





        String message = mChatMessageview.getText().toString();
        if(!TextUtils.isEmpty(message)){
            String current_user_ref = "messages/"+mCurrentUserId+"/"+mChatUser;
            String chat_user_ref = "messages/"+mChatUser+"/"+mCurrentUserId;
            DatabaseReference user_message_push = mrootRef.child("messages").child(mCurrentUserId).child(mChatUser).push();
            String push_id = user_message_push.getKey();
            Map messageMap = new HashMap();
            messageMap.put("message",message);
            messageMap.put("seend",false);
            messageMap.put("type","text");
            messageMap.put("time",ServerValue.TIMESTAMP);
            messageMap.put("from",mCurrentUserId);
            messageMap.put("to",mChatUser);

            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref+"/"+push_id,messageMap);
            messageUserMap.put(chat_user_ref+"/"+push_id,messageMap);
            mChatMessageview.setText("");

            mrootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if(databaseError!=null){
                        Log.d("Chat_Log",databaseError.getMessage().toString());
                    }
                }
            });

        }
    }
}


