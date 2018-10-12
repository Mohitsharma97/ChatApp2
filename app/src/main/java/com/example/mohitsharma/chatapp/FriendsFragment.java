package com.example.mohitsharma.chatapp;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.api.Api;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

     private RecyclerView mFriendsList;
     private DatabaseReference mFriendsDatabase;
     private FirebaseAuth mAuth;
     private String mCurrent_user_id;
     private View mMainView;
     private DatabaseReference mUsersDatabase;


    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView =  inflater.inflate(R.layout.fragment_friends, container, false);

        mFriendsList = (RecyclerView) mMainView.findViewById(R.id.friends_list);
        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getCurrentUser().getUid();
        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id);
        mFriendsDatabase.keepSynced(true);
       // mUsersDatabase.keepSynced(true);
        mUsersDatabase =FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);
        //mUsersDatabase.keepSynced(true);
        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));

        return mMainView;
    }


    @Override
    public void onStart(){
        super.onStart();

        FirebaseRecyclerAdapter<Friends,FriendsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(
                Friends.class,
                R.layout.users_single_layout,
                FriendsViewHolder.class,
                mFriendsDatabase
        ) {
            @Override
            protected void populateViewHolder(final FriendsViewHolder viewHolder, Friends friends, int position) {
                viewHolder.setDate(friends.getDate());
                final String list_user_id = getRef(position).getKey();
                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        final String username = dataSnapshot.child("name").getValue().toString();
                        String userThumb = dataSnapshot.child("thum_image").getValue().toString();
                        if(dataSnapshot.hasChild("online")) {
                            Boolean useronline = (boolean)dataSnapshot.child("online").getValue();
                            viewHolder.setuseronline(useronline);
                        }

                        viewHolder.setName(username);
                        viewHolder.setImage(userThumb);



                        viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[] = new CharSequence[]{"Open Profile","Send message"};
                                AlertDialog.Builder builder= new AlertDialog.Builder(getContext());
                                builder.setTitle("Select options");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {
                                              //event after clicking item
                                        if(i==0){
                                            Intent profileintent = new Intent(getContext(),ProfileActivity.class);
                                            profileintent.putExtra("user_id",list_user_id);
                                            startActivity(profileintent);
                                        }if(i==1){
                                                     Intent chatIntent = new Intent(getContext(),ChatActivity.class);
                                                     chatIntent.putExtra("user_id",list_user_id);
                                                     chatIntent.putExtra("user_name",username);
                                                     startActivity(chatIntent);
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        };
        mFriendsList.setAdapter(firebaseRecyclerAdapter);
    }

 public static class FriendsViewHolder extends RecyclerView.ViewHolder{
        View mview;
        public FriendsViewHolder(View itemview){
            super(itemview);
            mview = itemview;

        }

        public void setDate(String date){
            TextView userNameView = (TextView) mview.findViewById(R.id.textView3);
            userNameView.setText(date);
        }
        public void setName(String name){
            TextView userNameView = (TextView)mview.findViewById(R.id.textView);
            userNameView.setText(name);
        }

     public void setImage(final String thum_image){
         final CircleImageView usersimage = (CircleImageView)mview.findViewById(R.id.circleImageView);
         Picasso.get().load(thum_image).networkPolicy(NetworkPolicy.OFFLINE).into(usersimage, new Callback() {
             @Override
             public void onSuccess() {

             }

             @Override
             public void onError(Exception e) {
                 Picasso.get().load(thum_image).into(usersimage);
             }
         });


     }

     public void setuseronline(Boolean online_status){
         CircleImageView useronlineview = (CircleImageView)mview.findViewById(R.id.onlinestate);
         if(online_status.equals("true")){
             useronlineview.setVisibility(itemView.VISIBLE);
         }else {
             useronlineview.setVisibility(itemView.INVISIBLE);
         }
     }
 }
}
