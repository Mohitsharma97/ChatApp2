package com.example.mohitsharma.chatapp;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.iid.FirebaseInstanceId;

import java.security.Provider;

import static com.google.android.gms.common.util.WorkSourceUtil.TAG;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Toolbar mtoolbar;
    private ViewPager viewPager;
    private  SectionPagerAdapter SectionPagerAdapter;
    private TabLayout mTablayout;
    private DatabaseReference mUserRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        mtoolbar = (Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Chat App");
        if(mAuth.getCurrentUser()!=null) {

             mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        }
        viewPager  = (ViewPager)findViewById(R.id.tabpager);
        SectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(SectionPagerAdapter);
        mTablayout = (TabLayout)findViewById(R.id.main_tabs);
        mTablayout.setupWithViewPager(viewPager);



    //    String token = FirebaseInstanceId.getInstance().getToken();
     //   Log.d(TAG,"New_Token"+token);
      //  Toast.makeText(MainActivity.this,"Token Sent Successfully",Toast.LENGTH_SHORT).show();


    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser==null)
        {
            LogOut();

        }else {
            mUserRef.child("online").setValue(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser currentuser = mAuth.getCurrentUser();
        if(currentuser!=null) {
            mUserRef.child("online").setValue(false);

            mUserRef.child("LastSeen").setValue(ServerValue.TIMESTAMP);
        }
    }

    private void LogOut() {
        Intent StartIntent = new Intent(MainActivity.this,StartActivity.class);
        startActivity(StartIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {



        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId()==R.id.main_logout_btn){
            FirebaseAuth.getInstance().signOut();
            LogOut();
        }
        if(item.getItemId()==R.id.main_settings_btn){
            Intent settingsintent = new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(settingsintent);
        }
        if(item.getItemId()==R.id.main_all_btn){
            Intent Usersintent = new Intent(MainActivity.this,UsersActivity.class);
            startActivity(Usersintent);
        }



        return true;
    }
}
