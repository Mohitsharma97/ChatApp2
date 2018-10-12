package com.example.mohitsharma.chatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView mUserslist;
    private DatabaseReference mUsersDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        mAuth = FirebaseAuth.getInstance();
        mToolbar = (Toolbar)findViewById(R.id.Users_toolbar);
        mUserslist = (RecyclerView)findViewById(R.id.Users_list);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);





        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mUserslist.setHasFixedSize(true);
        mUserslist.setLayoutManager(new LinearLayoutManager(this));
    }


    @Override
    protected void onStart() {
        super.onStart();


        FirebaseRecyclerAdapter<Users,UsersviewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users,UsersviewHolder>(
                Users.class
                ,R.layout.users_single_layout,
                UsersviewHolder.class,
                mUsersDatabase
        ) {
            @Override
            protected void populateViewHolder(UsersviewHolder viewHolder, Users users, int i) {
                viewHolder.setName(users.getName());
                viewHolder.setStatus(users.getStatus());
                viewHolder.setImage(users.getThum_image());

                final String user_id = getRef(i).getKey();


                viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent profile_intent = new Intent(UsersActivity.this,ProfileActivity.class);
                        profile_intent.putExtra("user_id",user_id);
                        startActivity(profile_intent);
                    }
                });
            }
        };


        mUserslist.setAdapter(firebaseRecyclerAdapter);





    }








    public static class UsersviewHolder extends RecyclerView.ViewHolder{


        View mview;

        public UsersviewHolder(View itemView) {
            super(itemView);

            mview = itemView;

        }
        public void setName(String name){

            TextView userName = (TextView)mview.findViewById(R.id.textView);
            userName.setText(name);
        }

        public void setStatus(String status){
            TextView usersstatus = (TextView)mview.findViewById(R.id.textView3);
            usersstatus.setText(status);
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
    }

}
