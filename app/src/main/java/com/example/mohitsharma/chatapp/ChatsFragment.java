package com.example.mohitsharma.chatapp;


import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {

    private RecyclerView mConnList;
    private DatabaseReference mConvDatabase,mMessageDatabase;
    private FirebaseAuth mAuth;
    private String mCurrent_user_id;
    private View mMainView;
    private DatabaseReference mUsersDatabase;
    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView =  inflater.inflate(R.layout.fragment_chats, container, false);

        mConnList = (RecyclerView)mMainView.findViewById(R.id.conv_list);
        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getCurrentUser().getUid();
        mConvDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(mCurrent_user_id);
        mConvDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mMessageDatabase = FirebaseDatabase.getInstance().getReference().child("messages").child(mCurrent_user_id);
        mUsersDatabase.keepSynced(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mConnList.setHasFixedSize(true);
        mConnList.setLayoutManager(linearLayoutManager);


        return mMainView;
    }


    @Override
    public void onStart(){
        super.onStart();
        Query conversationQuery = mConvDatabase.orderByChild("timestamp");

        FirebaseRecyclerAdapter<Conv,ConViewHolder> firebaseConvAdapter = new FirebaseRecyclerAdapter<Conv, ConViewHolder>(
                Conv.class,
                R.layout.users_single_layout,
                ConViewHolder.class,
                conversationQuery
        ) {
            @Override
            protected void populateViewHolder(final ConViewHolder viewHolder, final Conv model, int position) {
                final String list_user_id = getRef(position).getKey();
                Query lastMessageQuery = mMessageDatabase.child(list_user_id).limitToLast(1);
                lastMessageQuery.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        String data = dataSnapshot.child("message").getValue().toString();

                        String message_type = dataSnapshot.child("type").getValue().toString();
                        if(data.equals("Users Status")){data="Send Message";}
                        if(message_type.equals("text")){
                        viewHolder.setMessage(data,model.isSeen());}else{ viewHolder.setMessage("Photo",model.isSeen());}



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
                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        final String username = dataSnapshot.child("name").getValue().toString();
                        String userThumb = dataSnapshot.child("thum_image").getValue().toString();
                        if(dataSnapshot.hasChild("online")){
                            String useronline= dataSnapshot.child("online").getValue().toString();

                            viewHolder.setUserOnline(useronline);
                        }
                        viewHolder.setName(username);
                        viewHolder.setUserimage(userThumb,getContext());
                        viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent chatIntent = new Intent(getContext(),ChatActivity.class);
                                chatIntent.putExtra("user_id",list_user_id);
                                chatIntent.putExtra("user_name",username);

                                startActivity(chatIntent);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        };
        mConnList.setAdapter(firebaseConvAdapter);

    }
    public static class ConViewHolder extends RecyclerView.ViewHolder{
        View mview;
        public ConViewHolder(View itemview){
            super(itemview);
            mview = itemView;
        }

        public void setMessage(String message,boolean isSeend){
            TextView userStatusView = (TextView) mview.findViewById(R.id.textView3);
            userStatusView.setText(message);
            if(!isSeend){
                userStatusView.setTypeface(userStatusView.getTypeface(), Typeface.BOLD);
            }else {  userStatusView.setTypeface(userStatusView.getTypeface(), Typeface.NORMAL);}
        }
        public void setName(String name){
            TextView username = (TextView)mview.findViewById(R.id.textView);
            username.setText(name);
        }
        public void setUserimage(final String thum_image, Context ctx){
            final CircleImageView userImageView = (CircleImageView)mview.findViewById(R.id.circleImageView);
            Picasso.get().load(thum_image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profile).into(userImageView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso.get().load(thum_image).placeholder(R.drawable.profile).into(userImageView);
                }
            });

        }
        public void setUserOnline(String online_status){
            ImageView useronlineView=(ImageView)mview.findViewById(R.id.onlinestate);
            if(online_status.equals("true")){
                useronlineView.setVisibility(View.VISIBLE);
            }else {
                useronlineView.setVisibility(View.INVISIBLE);
            }
        }
    }

}
