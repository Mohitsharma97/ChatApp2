package com.example.mohitsharma.chatapp;


import android.annotation.SuppressLint;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
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

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {

    private View mMainView;
    private DatabaseReference mFriendRequestdatabase,mFriendcheck;
    private DatabaseReference mUsersDatabase;
    private RecyclerView mFriendsList;
    private FirebaseUser mCurrentUser;
    private ImageView msadimage;
    private TextView mfriend;



    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_requests, container, false);
        mFriendsList = (RecyclerView) mMainView.findViewById(R.id.friends_list);
        msadimage = (ImageView)mMainView.findViewById(R.id.nofiend);
        mfriend = (TextView)mMainView.findViewById(R.id.nofriendavailable);
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mFriendRequestdatabase = FirebaseDatabase.getInstance().getReference().child("Friend_request").child(mCurrentUser.getUid());
       /* if(mFriendRequestdatabase!=null){
            mfriend.setVisibility(View.GONE);
            msadimage.setVisibility(View.GONE);
        }else{
            mfriend.setVisibility(View.VISIBLE);
            msadimage.setVisibility(View.VISIBLE);
        } */

        mUsersDatabase =FirebaseDatabase.getInstance().getReference().child("Users");
        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));

        return mMainView;

    }
    @Override
    public void onStart(){
        super.onStart();

        FirebaseRecyclerAdapter<FriendsRequest,FriendsRequestViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<FriendsRequest, FriendsRequestViewHolder>(
                FriendsRequest.class,
                R.layout.users_single_layout,
                FriendsRequestViewHolder.class,
                mFriendRequestdatabase
        ) {
            @Override
            protected void populateViewHolder(final FriendsRequestViewHolder viewHolder, FriendsRequest friends, int position) {
                viewHolder.setDate(friends.getRequest_type());
                final String list_user_id = getRef(position).getKey();




                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        mfriend.setVisibility(View.GONE);
                        msadimage.setVisibility(View.GONE);


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
                                Intent profileintent = new Intent(getContext(),ProfileActivity.class);
                                profileintent.putExtra("user_id",list_user_id);
                                startActivity(profileintent);

                            }
                        });

                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {


                        mfriend.setVisibility(View.VISIBLE);
                        msadimage.setVisibility(View.VISIBLE);


                    }
                });


            }
        };
        mFriendsList.setAdapter(firebaseRecyclerAdapter);
    }
    public static class FriendsRequestViewHolder extends RecyclerView.ViewHolder{
        View mview;
        public FriendsRequestViewHolder(View itemview){
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
