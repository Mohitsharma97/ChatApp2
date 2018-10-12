package com.example.mohitsharma.chatapp;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {


    private List<Messages> mMessageList;
    private  FirebaseAuth mAuth;
    private DatabaseReference mDatabasereference,toDatabasereference;


    public MessageAdapter(List<Messages> mMessageList) {
        this.mMessageList = mMessageList;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_layout, parent, false);
        return new MessageViewHolder(v);
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText,messagetime;
        public CircleImageView profileImage;
        public ImageView messageImage;

        public MessageViewHolder(View view) {

            super(view);

            messageText = (TextView) view.findViewById(R.id.message_text_layout);
            messageImage = (ImageView)view.findViewById(R.id.image_message);
            profileImage = (CircleImageView) view.findViewById(R.id.message_profile_layout);
            messagetime=(TextView)view.findViewById(R.id.message_date);
        }

    }
        @Override
        public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {

        mAuth = FirebaseAuth.getInstance();
           String current_user_id = mAuth.getCurrentUser().getUid();
            final Messages c = mMessageList.get(i);
            String from_user = c.getFrom();
            String messagetype = c.getType();
            String messageto = c.getTo();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm a' 'MM/dd", Locale.US);
            long messagetime = c.getTime();

         //   final String messageshown = c.getMessage();
            mDatabasereference = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);
            toDatabasereference = FirebaseDatabase.getInstance().getReference().child("Users").child(messageto);
            if(from_user.equals(current_user_id)){
                mDatabasereference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String thumb_image = dataSnapshot.child("thum_image").getValue().toString();
                        Picasso.get().load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profile).into(viewHolder.profileImage, new Callback() {
                            @Override
                            public void onSuccess() {
                                Picasso.get().load(thumb_image).placeholder(R.drawable.profile).into(viewHolder.profileImage);
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                if(messagetype.equals("text")){
                viewHolder.messageText.setBackgroundColor(Color.WHITE);

                viewHolder.messageText.setTextColor(Color.BLACK);
                viewHolder.messageImage.setVisibility(View.INVISIBLE);
                    viewHolder.messagetime.setText(sdf.format(new Date(c.getTime())));
                    viewHolder.messageText.setText(c.getMessage());}


                    if(messagetype.equals("image")){  viewHolder.messageText.setVisibility(View.INVISIBLE);
                        // viewHolder.messageText.setBackgroundColor(Color.WHITE);
                        Picasso.get().load(c.getMessage()).networkPolicy(NetworkPolicy.OFFLINE).into(viewHolder.messageImage, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get().load(c.getMessage()).into(viewHolder.messageImage);
                            }
                        });}

            }else{
                mDatabasereference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String thumb_image = dataSnapshot.child("thum_image").getValue().toString();
                        Picasso.get().load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profile).into(viewHolder.profileImage, new Callback() {
                            @Override
                            public void onSuccess() {
                                Picasso.get().load(thumb_image).placeholder(R.drawable.profile).into(viewHolder.profileImage);
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                if(messagetype.equals("text")){
                viewHolder.messageText.setBackgroundResource(R.drawable.message_text_background);
                viewHolder.messageText.setTextColor(Color.WHITE);
                viewHolder.messagetime.setText(sdf.format(new Date(c.getTime())));
                viewHolder.messageImage.setVisibility(View.INVISIBLE);
                viewHolder.messageText.setText(c.getMessage());
                }
                if(messagetype.equals("image")){  viewHolder.messageText.setVisibility(View.INVISIBLE);
                    // viewHolder.messageText.setBackgroundColor(Color.WHITE);
                    Picasso.get().load(c.getMessage()).networkPolicy(NetworkPolicy.OFFLINE).into(viewHolder.messageImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(c.getMessage()).into(viewHolder.messageImage);
                        }
                    });}

                    if(messagetype.equals("image")){
                    viewHolder.messageText.setVisibility(View.INVISIBLE);
                    // viewHolder.messageText.setBackgroundColor(Color.WHITE);
                    Picasso.get().load(c.getMessage()).networkPolicy(NetworkPolicy.OFFLINE).into(viewHolder.messageImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(c.getMessage()).into(viewHolder.messageImage);
                        }
                    });}
            }

           // viewHolder.timetext.setText(c.getTime());
        }

        @Override
        public int getItemCount() {
            return mMessageList.size();
        }

    }
